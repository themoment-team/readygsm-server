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

    private final ApplicationRepository applicationRepository;

    public byte[] execute(Long activityId) {
        List<ApplicationJpaEntity> applications = applicationRepository.findAllByActivity_Id(activityId);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("신청자 목록");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            for (int i = 0; i < applications.size(); i++) {
                ApplicationJpaEntity app = applications.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(COL_NAME).setCellValue(app.getName());
                row.createCell(COL_GRADE).setCellValue(app.getGrade());
                row.createCell(COL_CLASS_NUMBER).setCellValue(app.getClassNumber());
                row.createCell(COL_NUMBER).setCellValue(app.getNumber());
                row.createCell(COL_SCHOOL_NAME).setCellValue(app.getSchoolName());
                row.createCell(COL_PHONE_NUMBER).setCellValue(app.getPhoneNumber());
                row.createCell(COL_FAMILY_PHONE_NUMBER).setCellValue(app.getFamilyPhoneNumber());
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성에 실패했습니다.", e);
        }
    }
}
