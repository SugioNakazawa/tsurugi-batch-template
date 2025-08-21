package jp.gr.java_conf.nkzw.tbt.tools.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.tsurugidb.iceaxe.TsurugiConnector;

public class TgWorkbook {
    private Workbook exBook;
    private List<TgSheet> tgSheetList;

    public static TgWorkbook buider(InputStream is, TsurugiConnector connector)
            throws EncryptedDocumentException, IOException {
        return new TgWorkbook(WorkbookFactory.create(is), connector);
    }

    private TgWorkbook(Workbook book, TsurugiConnector connector) {
        this.exBook = book;
        this.tgSheetList = new ArrayList<TgSheet>();
        for (int i = 0; i < this.exBook.getNumberOfSheets(); i++) {
            try {
                this.tgSheetList.add(new TgSheet(exBook.getSheetAt(i), connector));
            } catch (InstantiationException e) {
                // TgSheetが生成できないときはスキップ
            }
        }
    }

    public Workbook getExBook() {
        return this.exBook;
    }

    public List<TgSheet> getTgSheetList() {
        return tgSheetList;
    }
}
