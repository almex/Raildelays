package be.raildelays.batch.poi;

/**
* Created by soumagn on 7/06/2014.
*/
public enum Format {
    OLE2(".xls"), OOXML(".xlsx");

    private String fileExtension;

    Format(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
