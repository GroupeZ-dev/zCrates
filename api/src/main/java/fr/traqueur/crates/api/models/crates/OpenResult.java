package fr.traqueur.crates.api.models.crates;

import org.jetbrains.annotations.Nullable;

/**
 * Result of attempting to open a crate.
 */
public record OpenResult(Status status, @Nullable OpenCondition failedCondition) {

    public enum Status {
        SUCCESS,
        NO_KEY,
        CONDITION_FAILED,
        EVENT_CANCELLED
    }

    public static OpenResult success() {
        return new OpenResult(Status.SUCCESS, null);
    }

    public static OpenResult noKey() {
        return new OpenResult(Status.NO_KEY, null);
    }

    public static OpenResult conditionFailed(OpenCondition condition) {
        return new OpenResult(Status.CONDITION_FAILED, condition);
    }

    public static OpenResult eventCancelled() {
        return new OpenResult(Status.EVENT_CANCELLED, null);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}