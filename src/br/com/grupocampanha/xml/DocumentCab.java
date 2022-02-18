/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.grupocampanha.xml;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jfc
 */
public class DocumentCab {

    Map<String, String> propeties = new HashMap();

    public DocumentCab() {
        propeties.put("version", "1.0");
    }

    public DocumentCab(String version) {
        if (version == null || version.isEmpty())
            throw new RuntimeException("versao nao pode estar vazia");
        propeties.put("version", version);
    }

    public void changePropetie(String key, String value) {
        if (key == null || key.isEmpty())
            throw new RuntimeException("key sem valor");
        if (value == null)
            throw new RuntimeException("value nulo");
        if (key.equals("version") && value.isEmpty())
            throw new RuntimeException("versao nao pode estar vazia");
        this.propeties.replace(key, value);
    }

    public DocumentCab addPropetie(String key, String value) {
        if (key == null || key.isEmpty())
            throw new RuntimeException("key sem valor");
        if (value == null)
            throw new RuntimeException("value nulo");
        if (key.equals("version"))
            throw new RuntimeException("nao e possivel adicionar duas versoes");
        this.propeties.put(key, value);
        return this;
    }

    public void removePropetie(String key) {
                if (key == null || key.isEmpty())
            throw new RuntimeException("key sem valor");
        if (key.equals("version"))
            throw new RuntimeException("nao e possivel remover a versao");
        this.propeties.remove(key);
    }

    @Override
    public String toString() {
        String s = "<?xml ";
        String[] k = this.propeties.keySet().toArray(new String[0]);
        String[] v = this.propeties.values().toArray(new String[0]);
        for (int i = 0; i < this.propeties.size(); i++)
            s += k[i] + " = '" + v[i] + "' ";
        s += "?>";
        return s;
    }
}
