package pl.jrj.dsm;

import javax.ejb.Remote;

@Remote
public interface IDSManagerRemote {
    public String getDS();
} 