package utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

// ExcelUtils reads data from .xlsx files
// Used for data driven testing — credentials come
// from Excel, not hardcoded in Java

public class ExcelUtils {

    private Workbook workbook;
    private Sheet sheet;

    // Constructor opens the file and sheet
    public ExcelUtils(String filePath, String sheetName) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            fis.close();
            System.out.println("Excel loaded: " + filePath);
        } catch (IOException e) {
            System.out.println("ERROR: Cannot open Excel: " + filePath);
            e.printStackTrace();
        }
    }

    // getCellData returns value of one specific cell
    // rowNum 0 = header row, 1 = first data row
    // colNum 0 = first column, 1 = second column
    public String getCellData(int rowNum, int colNum) {
        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) return "";

            Cell cell = row.getCell(colNum);
            if (cell == null) return "";

            // handle different cell types properly
            if (cell.getCellType() == CellType.NUMERIC) {
                return String.valueOf(
                    (long) cell.getNumericCellValue());
            }
            return cell.toString().trim();

        } catch (Exception e) {
            return "";
        }
    }

    // getRowCount returns number of data rows
    // (not counting header row)
    public int getRowCount() {
        return sheet.getLastRowNum();
    }

    public void close() {
        try {
            if (workbook != null) workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}