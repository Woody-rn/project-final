package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.Handlers;
import com.javarush.jira.bugtracking.task.to.ActivityTo;
import com.javarush.jira.common.error.DataConflictException;
import com.javarush.jira.common.error.NotFoundException;
import com.javarush.jira.login.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.javarush.jira.bugtracking.task.TaskUtil.getLatestValue;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final TaskRepository taskRepository;

    private final Handlers.ActivityHandler handler;

    private static void checkBelong(HasAuthorId activity) {
        if (activity.getAuthorId() != AuthUser.authId()) {
            throw new DataConflictException("Activity " + activity.getId() + " doesn't belong to " + AuthUser.get());
        }
    }

    @Transactional
    public Activity create(ActivityTo activityTo) {
        checkBelong(activityTo);
        Task task = taskRepository.getExisted(activityTo.getTaskId());
        if (activityTo.getStatusCode() != null) {
            task.checkAndSetStatusCode(activityTo.getStatusCode());
        }
        if (activityTo.getTypeCode() != null) {
            task.setTypeCode(activityTo.getTypeCode());
        }
        return handler.createFromTo(activityTo);
    }

    @Transactional
    public void update(ActivityTo activityTo, long id) {
        checkBelong(handler.getRepository().getExisted(activityTo.getId()));
        handler.updateFromTo(activityTo, id);
        updateTaskIfRequired(activityTo.getTaskId(), activityTo.getStatusCode(), activityTo.getTypeCode());
    }

    @Transactional
    public void delete(long id) {
        Activity activity = handler.getRepository().getExisted(id);
        checkBelong(activity);
        handler.delete(activity.id());
        updateTaskIfRequired(activity.getTaskId(), activity.getStatusCode(), activity.getTypeCode());
    }

    private void updateTaskIfRequired(long taskId, String activityStatus, String activityType) {
        if (activityStatus != null || activityType != null) {
            Task task = taskRepository.getExisted(taskId);
            List<Activity> activities = handler.getRepository().findAllByTaskIdOrderByUpdatedDesc(task.id());
            if (activityStatus != null) {
                String latestStatus = getLatestValue(activities, Activity::getStatusCode);
                if (latestStatus == null) {
                    throw new DataConflictException("Primary activity cannot be delete or update with null values");
                }
                task.setStatusCode(latestStatus);
            }
            if (activityType != null) {
                String latestType = getLatestValue(activities, Activity::getTypeCode);
                if (latestType == null) {
                    throw new DataConflictException("Primary activity cannot be delete or update with null values");
                }
                task.setTypeCode(latestType);
            }
        }
    }

    public Duration calculateWorkTime(Long id) {
        return calculateTimeBetweenStatuses(id,
                StatusCode.READY_FOR_REVIEW,
                StatusCode.IN_PROGRESS
        );
    }

    public Duration calculateTestTime(Long id) {
        return calculateTimeBetweenStatuses(id,
                StatusCode.DONE,
                StatusCode.READY_FOR_REVIEW);
    }

    private Duration calculateTimeBetweenStatuses(Long id, StatusCode startStatus, StatusCode endStatus) {
        List<Activity> activityList = handler.getRepository()
                .findAllByTaskId(id);
        LocalDateTime startTime = getLastStatusTime(activityList, startStatus.getStatusCode());
        LocalDateTime endTime = getLastStatusTime(activityList, endStatus.getStatusCode());
        if (endTime.isAfter(startTime)) {
            throw new DataConflictException(
                    endStatus + " time (" + endTime + ") is after " +
                            startStatus + " time (" + startTime + ")");
        }
        return Duration.between(endTime, startTime);
    }

    private LocalDateTime getLastStatusTime(List<Activity> activityList, String statusCode) {
        if (activityList.isEmpty()) {
            throw new NotFoundException("No activities found or task not found");
        }
        return activityList.stream()
                .filter(activity -> statusCode.equals(activity.getStatusCode()))
                .map(Activity::getUpdated)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException("No " + statusCode + " status found"));
    }
}
