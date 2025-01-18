package io.hahn_software.emrs.dtos;

public record OperationResult(
    int affectedRecords,
    String message
) {



    public static OperationResult of(int affectedRecords) {
        return new OperationResult(affectedRecords, "Operation completed successfully.");
    }


    public static OperationResult of(int affectedRecords, String message) {
        return new OperationResult(affectedRecords, message);
    }

}