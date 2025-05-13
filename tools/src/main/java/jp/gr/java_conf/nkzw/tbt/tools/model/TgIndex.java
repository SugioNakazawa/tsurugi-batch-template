package jp.gr.java_conf.nkzw.tbt.tools.model;

public class TgIndex {

    private String indexName;
    private String indexColNames;

    public TgIndex(String indexNameCell, String indexColNames) {
        this.indexName = indexNameCell;
        this.indexColNames = indexColNames;
    }

    public String getIndexName() {
        return indexName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TgIndex indexName = ").append(indexName);
        sb.append(", indexColNames = ").append(indexColNames).append("\n");
        return sb.toString();
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexColNames() {
        return indexColNames;
    }

    public void setIndexColNames(String indexColNames) {
        this.indexColNames = indexColNames;
    }

    public String getIndexDef(String tableName) {
        var sb = new StringBuilder();
        sb.append("CREATE INDEX ").append(indexName).append(" ON ");
        sb.append(tableName).append(" (").append(indexColNames).append(");");
        return sb.toString();
    }

}
