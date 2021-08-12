
import br.com.grupocampanha.xml.Document;
import br.com.grupocampanha.xml.Node;
import br.com.grupocampanha.xml.XML;
import br.com.grupocampanha.xml.exceptions.IllegalFileException;
import br.com.grupocampanha.xml.exceptions.IllegalNodePropetyNameException;
import br.com.grupocampanha.xml.exceptions.IllegalNodePropetyValueException;
import br.com.grupocampanha.xml.exceptions.InsertNodeException;
import br.com.grupocampanha.xml.exceptions.InsertNodeValueException;
import br.com.grupocampanha.xml.exceptions.UnformattedXmlException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jfc
 */
public class teste {
    public static void main(String[] args) throws IOException, InsertNodeException, InsertNodeValueException, UnformattedXmlException, IllegalNodePropetyNameException, IllegalNodePropetyValueException, IllegalFileException{
    Document teste = XML.parse(new File("C:\\Users\\jfc\\Desktop\\aaaaaa\\teste123.xml"));
       // System.out.println(teste.node);
 //   teste.node.adicionarNode(new Node("animal"){{ setValor("egua");}});
   //Node aa =  teste.node.find(f -> f.getNome() != null && f.getNome().equals("UrlSankha"));
   Document teste2 = new Document("C:\\Users\\jfc\\Desktop\\aaaaaa\\tete345.xml");
   teste2.node =  teste.node;
   teste.write(teste2);
    }
}