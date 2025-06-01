--changeset add:data-activity-for-task99
INSERT INTO ACTIVITY (ID, AUTHOR_ID, TASK_ID, UPDATED, STATUS_CODE)
VALUES (1001, 2, 99, '2025-01-01 10:00:00', 'in_progress'),
       (1002, 2, 99, '2025-01-03 14:30:00', 'ready_for_review'),
       (1003, 2, 99, '2025-01-05 09:15:00', 'done');