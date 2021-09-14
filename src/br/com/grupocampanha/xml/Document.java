/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.exceptions.IllegalFileException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

/**
 *
 * @author jfc
 */
public class Document extends File {

    public Node node;

    public Document(String caminho) throws IllegalFileException {
        super(caminho);
    }

    public void write(File file) throws IOException {
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(file));
        writer.write(this.node.toString());
        writer.close();
    }

}
