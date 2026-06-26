package team.themoment.readygsmserver.domain.application.dto.response;

public record ExcelExportResult(
        String fileName,
        byte[] content
) {
}