/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupocampanha.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author jfc
 */

public class Document  {

    protected DocumentCab cab;
    private Node node;
    private String documentName;
    private File documentPath;


    public Document(String name) {
        cab = new DocumentCab("1.0");
        if (name == null || name.isEmpty())
            throw new RuntimeException("nome nao pode ser nulo nem vazido");
        this.documentName = name;
    }

    public Document(String name, DocumentCab cab) {
        if (name == null || name.isEmpty())
            throw new RuntimeException("nome nao pode ser nulo nem vazido");
        if (cab == null)
            throw new RuntimeException("o cabecalho nao pode ser nulo");
        this.documentName = name;
        this.cab = cab;
    }

    public DocumentCab getCab() {
        return this.cab;
    }

    public Node getRootNode() {
        return this.node;
    }

    public void setRootNode(Node node) {
        this.node = node;
    }

     public void writeTo(File file) throws IOException{
         this.write(file, false);
     }
    public void write(File file, boolean useDocumentName) throws IOException {
        File newFile;
        if (useDocumentName) {
            String path = file.getPath();
            path = path.charAt(path.length() - 1) == '/' || path.charAt(path.length() - 1) == '\\' ? path.substring(0, path.length() - 1) : path;
            newFile = new File(path+"/"+documentName+".xml");
        } else
            newFile = file;

        String value = cab.toString() + "\n";
        value += node.toString();
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(newFile));
        writer.write(value);
        writer.close();
    }
}
