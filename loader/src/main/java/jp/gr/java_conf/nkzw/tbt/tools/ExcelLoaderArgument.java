package jp.gr.java_conf.nkzw.tbt.tools;

import com.beust.jcommander.Parameter;

public class ExcelLoaderArgument {
    @Parameter(names = { "--help", "-h" }, arity = 0, description = "print this message", help = true)
    private Boolean help;

    @Parameter(names = { "--endpoint", "-c" }, arity = 1, description = "connect tsurugi endpont", required = false)
    private String endpont = System.getProperty("CVS_TG_ENDPOINT", "tcp://localhost:12345");

    @Parameter(names = { "--mode", "-m" }, arity = 1, description = "run mode [load | template | dump | compare]", required = false)
    private String mode = "load";

    @Parameter(names = { "--srcfile", "-s" }, arity = 1, description = "load file path", required = false)
    private String src;

    @Parameter(names = { "--templatefile", "-t" }, arity = 1, description = "template file path", required = false)
    private String templatePath = "template.xlsx";

    @Parameter(names = { "--table" }, arity = 1, description = "table name", required = false)
    private String tableName;

    public boolean isHelp() {
        return (this.help != null) && this.help;
    }

    public String getEndpont() {
        return endpont;
    }

    public Boolean getHelp() {
        return help;
    }

    public String getMode() {
        return mode;
    }

    public String getSrc() {
        return src;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public String getTableName() {
        return this.tableName;
    }

}
