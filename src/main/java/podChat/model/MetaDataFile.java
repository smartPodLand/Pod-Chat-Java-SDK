package podChat.model;

public class MetaDataFile {
    private FileMetaDataContent file;
    private long id;
    private String name;

    public FileMetaDataContent getFile() {
        return file;
    }

    public void setFile(FileMetaDataContent file) {
        this.file = file;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
