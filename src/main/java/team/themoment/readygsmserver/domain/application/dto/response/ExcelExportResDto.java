package team.themoment.readygsmserver.domain.application.dto.response;

public record ExcelExportResDto(
        String fileName,
        byte[] content
) {
}