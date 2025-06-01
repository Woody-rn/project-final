package com.javarush.jira.bugtracking.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCode {
    IN_PROGRESS("in_progress"),
    READY_FOR_REVIEW("ready_for_review"),
    DONE("done");

    private final String statusCode;
}
