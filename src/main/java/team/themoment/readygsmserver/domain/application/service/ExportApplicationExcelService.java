package team.themoment.readygsmserver.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportApplicationExcelService {

    private static final String[] HEADERS = {"이름", "학년", "반", "번호", "학교명", "연락처", "보호자 연락처"};

    private static final int COL_NAME = 0;
    private static final int COL_GRADE = 1;
    private static final int COL_CLASS_NUMBER = 2;
    private static final int COL_NUMBER = 3;
    private static final int COL_SCHOOL_NAME = 4;
    private static final int COL_PHONE_NUMBER = 5;
    private static final int COL_FAMILY_PHONE_NUMBER = 6;

    // POI 컬럼 너비 단위: 1/256 문자 폭
    private static final int[] COL_WIDTHS = {
            10 * 256,  // 이름
            6 * 256,   // 학년
            6 * 256,   // 반
            6 * 256,   // 번호
            30 * 256,  // 학교명
            16 * 256,  // 연락처
            16 * 256   // 보호자 연락처
    };

    private final ApplicationRepository applicationRepository;

    public byte[] execute(Long activityId) {
        List<ApplicationJpaEntity> applications = applicationRepository.findAllByActivity_Id(activityId);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("신청자 목록");
            Sheet reserveSheet = workbook.createSheet("신청 대기자 목록");

            Row headerRow = sheet.createRow(0);
            Row reservedHeaderRow = reserveSheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
                reservedHeaderRow.createCell(i).setCellValue(HEADERS[i]);
            }

            int sheetRow = 1;
            int reserveSheetRow = 1;
            for (ApplicationJpaEntity app : applications) {
                if (app.isReserve()) {
                    createApplicationRow(reserveSheet, app, reserveSheetRow++);
                } else {
                    createApplicationRow(sheet, app, sheetRow++);
                }
            }

            for (int i = 0; i < COL_WIDTHS.length; i++) {
                sheet.setColumnWidth(i, COL_WIDTHS[i]);
                reserveSheet.setColumnWidth(i, COL_WIDTHS[i]);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성에 실패했습니다.", e);
        }
    }

    private void createApplicationRow(Sheet sheet, ApplicationJpaEntity app, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(COL_NAME).setCellValue(app.getName());
        row.createCell(COL_GRADE).setCellValue(app.getGrade());
        row.createCell(COL_CLASS_NUMBER).setCellValue(app.getClassNumber());
        row.createCell(COL_NUMBER).setCellValue(app.getNumber());
        row.createCell(COL_SCHOOL_NAME).setCellValue(app.getSchoolName());
        row.createCell(COL_PHONE_NUMBER).setCellValue(app.getPhoneNumber());
        row.createCell(COL_FAMILY_PHONE_NUMBER).setCellValue(app.getFamilyPhoneNumber());
    }
}
