package jp.gr.java_conf.nkzw.tbt.app.batch;

import com.beust.jcommander.Parameter;

public class TemplateBatchArgument {
    @Parameter(names = { "--help", "-h" }, arity = 0, description = "print this message", help = true)
    private Boolean help;
    public boolean isHelp() {
        return (this.help != null) && this.help;
    }
}
