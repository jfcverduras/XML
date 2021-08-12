package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.exceptions.IllegalNodePropetyNameException;
import br.com.grupocampanha.xml.exceptions.IllegalNodePropetyValueException;
import br.com.grupocampanha.xml.exceptions.InsertNodeException;
import br.com.grupocampanha.xml.exceptions.InsertNodeValueException;
import br.com.grupocampanha.xml.exceptions.NodeIndexOfBoundsException;
import br.com.grupocampanha.xml.exceptions.NonExistentNodeException;
import br.com.grupocampanha.xml.exceptions.ProprietyNotFoundException;
import java.util.HashMap;
import java.util.Map;
import br.com.grupocampanha.xml.interfaces.Find;

public class Node {

    private Map<String, String> propriedades = new HashMap<String, String>();
    private String valor;
    private Node[] nodes = new Node[0];
    private String nome;
    private int tamanho = 0;
    private int tamanhoAtributo = 0;

    public Node(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void mudarNome(String nome) {
        this.nome = nome;
    }

    public void adicionarNode(Node node) throws InsertNodeException {
        if (valor != null) {
            throw new InsertNodeException();
        }
        nodes = redimensionarArray(nodes, nodes.length + 1);
        nodes[nodes.length - 1] = node;
        tamanho++;
    }

    public void removerNode(Node node) throws NonExistentNodeException {

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].equals(node)) {
                for (int k = i; k < nodes.length - 1; i++) {
                    nodes[i] = nodes[i + 1];
                }
                nodes = (Node[]) redimensionarArray(nodes, nodes.length - 1);
                tamanho--;

            }
        }
        throw new NonExistentNodeException();
    }

    public void removerNode(int index) throws NodeIndexOfBoundsException {
        if (index > nodes.length - 1 || index < 0) {
            throw new NodeIndexOfBoundsException();
        }

        for (int i = index; i < nodes.length - 1; i++) {
            nodes[i] = nodes[i + 1];
        }
        nodes = (Node[]) redimensionarArray(nodes, nodes.length - 1);
        tamanho--;
    }

    public void setValor(String valor) throws InsertNodeValueException {
        if (nodes.length > 0) {
            throw new InsertNodeValueException();
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public String getPropriedade(String propriedade) {
        return propriedades.get(propriedade);
    }

    public String propriedadeAt(int index) {
        return (String) propriedades.values().toArray()[index];
    }

    public Node nodeAt(int index) throws NodeIndexOfBoundsException {
        try {
            return nodes[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NodeIndexOfBoundsException();
        }
    }

    public void adicionarPropriedade(String propriedade, String Valor) throws IllegalNodePropetyNameException  {
        if (Utilitario.contemCaracterEspecial(propriedade)) {
            throw new IllegalNodePropetyNameException();
        }
   
        this.propriedades.put(propriedade, Valor);
        tamanhoAtributo++;
    }

    public void removerPropriedade(String propriedade) throws ProprietyNotFoundException {
        Object[] keys = this.propriedades.keySet().toArray();

        boolean existe = false;
        for (Object o : keys) {
            if (((String) o).equals(propriedade)) {
                existe = true;
            }
        }
        if (!existe) {
            throw new ProprietyNotFoundException();
        }

        this.propriedades.remove(propriedade);
        tamanhoAtributo--;
    }

    private Node[] redimensionarArray(Node[] array, int novoTamanho) {
        Node[] novoArray = new Node[novoTamanho];
        for (int i = 0; i < (novoTamanho <= 0 ? novoArray.length : array.length); i++) {
            novoArray[i] = array[i];
        }
        return novoArray;
    }

    @Override
   public String toString(){
   return toStringImplementation(1);
   
   }
    public String toStringImplementation(int tabulacao) {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + nome);
        if (propriedades != null) {
            Object[] chaves = propriedades.keySet().toArray();
            Object[] valores = propriedades.values().toArray();
            for (int i = chaves.length -1; i >= 0; i--) {
                if (((String) valores[i]).contains("\"")) {
                    sb.append(" " + chaves[i] + "='" + valores[i] + "'");
                } else {
                    sb.append(" " + chaves[i] + "=\"" + valores[i] + "\"");
                }
            }
        }

        if (valor == null && nodes.length == 0) {
            sb.append("/>");
            return sb.toString();
        }
        if(nome.toLowerCase().equals("?xml")){
        sb.append("?>");
        }else{
        sb.append(">");
        }
        
        if (valor != null) {
            sb.append(valor);
             if(!nome.toLowerCase().equals("?xml")){
        sb.append("</" + nome + ">");
        }
        }else{
            
        for (int i = 0; i < nodes.length; i++) {

            sb.append("\n"+gerarTabulacao(tabulacao) + nodes[i].toStringImplementation(tabulacao +1));
        }
        
         if(!nome.toLowerCase().equals("?xml")){
        sb.append("\n"+gerarTabulacao(tabulacao-1) + "</" + nome + ">");
        }
        }
        return sb.toString();
    }
private String gerarTabulacao(int qtd){
    String tabulacao = "";
for(int i =0 ; i< qtd;i++){
    tabulacao = tabulacao + "   ";
}
return tabulacao;
}

 public Node find(Find f){
  return findImplementation(f,this);
 }
    private Node findImplementation(Find f, Node node)  {

        if (f.find(node)) {
         return node;
        }

        for (int i = 0; i < node.nodes.length; i++) {
            if (node.nodes[i].size() > 0) {
                node =  findImplementation(f, node.nodes[i]);
            } else {
                if (f.find(node.nodes[i])) {
                    return node.nodes[i];
                }
            }

        }
        return node;
    }

    public Node[] findNodes(Find f) {

        return findNodesImplementation(f, this);
    }

    private Node[] findNodesImplementation(Find f, Node node) {
        Node[] nodes = new Node[0];

        if (f.find(node)) {
            nodes = new Node[1];
            nodes[0] = node;
        }

        for (int i = 0; i < node.nodes.length; i++) {
            if (node.nodes[i].size() > 0) {
                nodes = Utilitario.juntarNodes(nodes, findNodesImplementation(f, node.nodes[i]));
            } else {
                if (f.find(node.nodes[i])) {
                    nodes = redimensionarArray(nodes, nodes.length + 1);
                    nodes[nodes.length - 1] = node.nodes[i];
                }
            }

        }
        return nodes;
    }

    

    public int propriedadesSize() {
        return tamanhoAtributo;
    }

    public boolean contains(Find f) {
        for (int i = 0; i < nodes.length; i++) {
            if (f.find(nodes[i])) {
                return true;
            }
        }
        return false;

    }

    public int size() {
        return tamanho;
    }

}

class Utilitario {

    public static boolean contemCaracterEspecial(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c)) {
                return true;

            }

        }
        return false;
    }

    public static boolean contemCaracterEspecial(String string, boolean permirtir, char... caracter) {
        for (char c : string.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c)) {
                for (char i : caracter) {
                    if (i == c && !permirtir) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static Node[] juntarNodes(Node[] n1, Node[] n2) {
        int sizeNovoNode = n1.length + n2.length;
        Node[] novoArrayNode = new Node[sizeNovoNode];
        int i = 0;
        for (Node n : n1) {
            novoArrayNode[i] = n;
            i++;
        }
        for (Node n : n2) {
            novoArrayNode[i] = n;
            i++;
        }
        return novoArrayNode;
    }
}
