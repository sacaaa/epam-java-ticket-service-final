package com.epam.training.ticketservice.core.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A generic wrapper class to represent the result of an operation.
 *
 * @param <T> The type of data included in the result.
 */
@Getter
@AllArgsConstructor
public class Result<T> {

    /**
     * Indicates if the operation was successful.
     */
    private final boolean success;

    /**
     * An optional message providing additional context (e.g., error details).
     */
    private final String message;

    /**
     * The data associated with the operation's result, if any.
     */
    private final T data;

    /**
     * Creates a successful result with the specified data.
     *
     * @param data The data to include in the result.
     * @param <T> The type of the data.
     * @return A Result object indicating success.
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(true, null, data);
    }

    /**
     * Creates a failed result with the specified error message.
     *
     * @param message The error message.
     * @param <T> The type of the data (usually null for failures).
     * @return A Result object indicating failure.
     */
    public static <T> Result<T> failure(String message) {
        return new Result<>(false, message, null);
    }

}
