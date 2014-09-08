package be.raildelays.domain.xls;

import java.util.List;

public class ExcelSheet {


    private List<ExcelRow> excelRows;

    public List<ExcelRow> getExcelRows() {
        return excelRows;
    }

    public void setExcelRows(List<ExcelRow> excelRows) {
        this.excelRows = excelRows;
    }
}
