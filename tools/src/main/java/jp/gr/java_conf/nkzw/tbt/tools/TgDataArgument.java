package jp.gr.java_conf.nkzw.tbt.tools;

import com.beust.jcommander.Parameter;

public class TgDataArgument {

    @Parameter(names = { "--excel", "-e" }, //
            arity = 1, //
            description = "excel file path for input", //
            required = false)
    private String excelFileName;

    @Parameter(names = { "--datacount" }, //
            arity = 1, //
            description = "number of generate data", //
            required = false)
    private String dataCount = "10";

    @Parameter(names = { "--endpoint" }, //
            arity = 1, //
            description = "endpoint for tsurugidb", //
            required = false)
    private String endPoint = System.getProperty("CVS_TG_ENDPOINT", "tcp://localhost:12345");

    @Parameter(names = { "--sheet", "-s" }, //
            arity = 1, //
            description = "excel sheet for input", //
            required = false)
    private String excelSheetName = null;

    /**
     * Javaソースのパッケージ名
     */
    @Parameter(names = { "--javapackage" }, //
            arity = 1, //
            description = "java package name", //
            required = false)
    private String javaPackageName = "com.hoge";

    /**
     * ファイル出力パス。
     */
    @Parameter(names = { "--out", "-o" }, //
            arity = 1, //
            description = "output path for java source", //
            required = false)
    private String outPath = "./out";

    /**
     * テーブル情報出力抑制。
     * 生成されたテーブル情報をコンソールに出力しない。
     */
    @Parameter(names = { "--silent" }, //
            description = "no output console", //
            required = false)
    private Boolean silent = false;

    /**
     * SQL DDLを出力する。
     */
    @Parameter(names = { "--ddl" }, //
            description = "output sql ddl (in java package directory)", //
            required = false)
    private Boolean ddl = false;

    @Parameter(names = { "--help", "-h" }, //
            arity = 0, //
            description = "print this message", //
            help = true)
    private Boolean help;

    @Parameter(names = { "--createtable" }, //
            arity = 0, //
            description = "create table drop if exist")
    private Boolean createtable = false;

    @Parameter(names = { "--generatedata" }, //
            arity = 0, //
            description = "generate data to Tsurugi")
    private Boolean generatedata = false;

    // getter
    public String getExcelFileName() {
        return excelFileName;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getExcelSheetName() {
        return excelSheetName;
    }

    public boolean hasExcelSheetName() {
        if (this.excelSheetName == null) {
            return false;
        }
        return true;
    }

    public String getJavaPackageName() {
        return javaPackageName;
    }

    public String getOutPath() {
        return outPath;
    }

    public Boolean isSilent() {
        return (this.silent != null) && this.silent;
    }

    public Boolean isDdl() {
        return (this.ddl != null) && this.ddl;
    }

    public Boolean isHelp() {
        return (this.help != null) && this.help;
    }

    public Boolean isCreateTable() {
        return (this.createtable != null) && this.createtable;
    }

    public Boolean isGenerateData() {
        return (this.generatedata != null) && this.generatedata;
    }

    public int getDataCount() {
        return Integer.parseInt(this.dataCount);
    }
}