package com.lzq.dawn.util.rom;

/**
 * @Name :RomInfo
 * @Time :2022/8/29 15:20
 * @Author :  Lzq
 * @Desc :
 */
public final class RomInfo {
    private String name;
    private String version;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "RomInfo{name=" + name +
                ", version=" + version + "}";
    }
}
