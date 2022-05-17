package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.exceptions.InsertNodeException;
import br.com.grupocampanha.xml.exceptions.InsertNodeValueException;
import java.util.HashMap;
import java.util.Map;
import br.com.grupocampanha.xml.interfaces.Find;
import br.com.grupocampanha.xml.interfaces.NodeValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
public class Node implements NodeValue {
    
    protected Map<String, String> propriedades = new HashMap<String, String>();
    private NodeContent valor;
    private Node[] nodes = new Node[0];
    private String nome;
    private int tamanho = 0;
    private int tamanhoAtributo = 0;
    private List<NodeContent> comentarios = new ArrayList();
    private boolean isFechamento;
    
    protected Node() {
        this.isFechamento = false;
    }
   
    public Node(String nome) {
        this();
        if (nome == null || nome.isEmpty())
            throw new RuntimeException("Nome do node nao pode ser vazio ou nulo");
        
        this.nome = nome;
    }
    
    protected Node(String nome, boolean isFechamento) {
        this(nome);
        this.isFechamento = isFechamento;
    }
    
    protected boolean isFechamento() {
        return isFechamento;
    }
    
    public String getNome() {
        return nome;
    }
    
    public Node mudarNome(String nome) {
        this.nome = nome;
        return this;
    }
    
    public Node add(NodeValue nodev) {
        
        Node node = null;
        
        if (nodev instanceof Node) {
            node = (Node) nodev;
            if (valor != null)
                throw new InsertNodeException();
            nodes = redimensionarArray(nodes, nodes.length + 1);
            nodes[nodes.length - 1] = node;
            tamanho++;
        } else if (nodev instanceof NodeContent) {
            if (nodev.getValue().startsWith("<?xml") && nodev.getValue().endsWith("?>"))
                throw new InsertNodeValueException("nao e possivel adicionar um cabecalho dentro de um node");
            if (nodes.length > 0)
                throw new InsertNodeValueException();
            valor = (NodeContent) nodev;
            
        } else
            throw new InsertNodeValueException("Objeto nao contem uma instancia valida");
        return this;
    }

    /**
     * Isso é o mesmo que chamar o metodo add(NodeValue nodev)
     *
     * @param valor valor do node
     * @return retorna o mesmo node alterado
     */
    public Node add(String valor) {
        add(new NodeContent(valor, NodeType.VALUE));
        return this;
    }
    
    public void remove() {
        this.nodes = new Node[0];
        this.valor = null;
    }
    
    public void remove(Node node) {
        
        for (int i = 0; i < nodes.length; i++)
            if (nodes[i].equals(node)) {
                for (int k = i; k < nodes.length - 1; k++)
                    nodes[k] = nodes[k + 1];
                nodes = (Node[]) redimensionarArray(nodes, nodes.length - 1);
                tamanho--;
                break;
            }
    }
    
    public void remove(int index) {
        if (index > nodes.length - 1 || index < 0)
            return;
        
        for (int i = index; i < nodes.length - 1; i++)
            nodes[i] = nodes[i + 1];
        nodes = (Node[]) redimensionarArray(nodes, nodes.length - 1);
        tamanho--;
    }

    
    public void removeAllPropeties() {
        Set<String> keys = this.propriedades.keySet();
        keys.forEach(f -> {
            propriedades.remove(f);
        });
    }
    
    protected void clonePropeties(Node node) {
        this.propriedades = node.propriedades;
    }
    
    public void addComentario(String comentario) {
        this.comentarios.add(new NodeContent(comentario, NodeType.SPECIAL));
    }
    
    public void removeComentario(int i) {
        this.comentarios.remove(i);
    }
    
    public String getComentario(int i) {
        NodeContent n = this.comentarios.get(i);
        String value = null;
        if (n != null)
            value = n.getValue().substring(4);
        value = value.substring(0, value.length() - 3);
        return value;
    }
    
    public void setComentario(String comentario, int index) {
        this.comentarios.set(index, new NodeContent(comentario, NodeType.SPECIAL));
    }
    
    public String[] getAllComentarios() {
        String[] s = new String[this.comentarios.size()];
        for (int i = 0; i < s.length; i++)
            s[i] = this.comentarios.get(i).getValue();
        return s;
    }
    
    public void removeAllComentarios() {
        this.comentarios.removeAll(comentarios);
    }
    
    @Override
    public String getValue() {
        return valor.getValue();
    }
    
    public void forEach(Consumer<Node> a) {
        for (Node n : nodes)
            a.accept(n);
        
    }
    
    public String getPropriedade(String propriedade) {
        return propriedades.get(propriedade);
    }
    
    public String getPropriedade(int index) {
        return (String) propriedades.values().toArray()[index];
    }
    
    public Node get(int index) {
        return nodes[index];
    }
    
    public Node get(String path) {
        return getByString(this, path);
        
    }
    
    private Node getByString(Node nodePrincipal, String path) {
        class PS {
            
            int initNextNode;
            int nodePosition;
            String nomeNode;
            
            public PS(String path) {
                path = path.substring(path.indexOf("/") + 1);
                initNextNode = path.indexOf("/") + 1;
                String frag = path.substring(0, path.indexOf("/"));
                nodePosition = nodeIndex(frag);
                if (nodePosition == -1)
                    nomeNode = frag;
                else
                    nomeNode = frag.substring(0, frag.indexOf("["));
            }
            
            private int nodeIndex(String frag) {
                int initIndex;
                int endIndex;
                initIndex = frag.indexOf("[");
                if (initIndex == -1)
                    return -1;
                int qtdchar = frag.substring(0, initIndex + 1).length();
                endIndex = frag.substring(initIndex + 1).indexOf("]");
                if (endIndex == -1)
                    return -1;
                
                endIndex += qtdchar;
                
                String numeros = frag.substring(initIndex + 1, endIndex);
                if (!numeros.chars().allMatch(Character::isDigit))
                    return -1;
                return Integer.parseInt(numeros);
            }
            
        }
        PS ps = new PS(path);
        Node[] nods = Arrays.asList(nodePrincipal.nodes).stream().filter(f -> f.getNome().equals(ps.nomeNode)).collect(Collectors.toList()).toArray(new Node[0]);
        Node node;
        if (nods.length > 1)
            if (ps.nodePosition == -1)
                return null;
            else
                node = nods[ps.nodePosition];
        else if (nods.length == 0)
            return null;
        else
            node = nods[0];
        
        if (!path.substring(ps.initNextNode + 1).isEmpty())
            return getByString(node, path.substring(ps.initNextNode));
        else
            return node;
    }
    
    public Node adicionarPropriedade(String propriedade, String Valor) {
        this.propriedades.put(propriedade, Valor);
        tamanhoAtributo++;
        return this;
    }
    
    public void removerPropriedade(String propriedade) {
        
        this.propriedades.remove(propriedade);
        tamanhoAtributo--;
        
    }
    
    private Node[] redimensionarArray(Node[] array, int novoTamanho) {
        Node[] novoArray = new Node[novoTamanho];
        for (int i = 0; i < (novoTamanho > array.length ? array.length : novoTamanho); i++)
            novoArray[i] = array[i];
        return novoArray;
    }
    
    @Override
    public String toString() {
        return toStringImplementation(0, this);
    }
    
    private String toStringImplementation(int tabulacao, Node node) {
        String tab = gerarTabulacao(tabulacao);
        String comentarios = commentsToString(tab, node);
        String propeties = propetiesToString(node);
        StringBuilder sb = new StringBuilder();
        if (node.valor == null && comentarios.isEmpty() && node.nodes.length == 0 )
            sb.append("<").append(node.nome).append(propeties).append(" />").append("\n");
        
        if (node.valor != null && !comentarios.isEmpty())
            sb.append("<").append(node.nome).append(propeties).append(">").append("\n").append(comentarios).append(node.valor.getValue())
                    .append("</").append(node.nome).append(">").append("\n");
        if (node.valor != null && comentarios.isEmpty())
            sb.append("<").append(node.nome).append(propeties).append(">").append(node.valor.getValue())
                    .append("</").append(node.nome).append(">").append("\n");
        
        if (node.valor == null && node.nodes.length == 0 && !comentarios.isEmpty())
            sb.append("<").append(node.nome).append(propeties).append(">").append("\n").append(comentarios)
                    .append("</").append(node.nome).append(">").append("\n");
        
        if (node.nodes.length > 0) {
            String insideNodes = "";
            for (Node n : node.nodes)
                insideNodes += toStringImplementation(tabulacao + 1, n) + "\n";
            
            sb.append("<").append(node.nome).append(propeties).append(">");
            if (!comentarios.isEmpty())
                sb.append("\n").append(comentarios);
            else
                sb.append("\n");
            sb.append(insideNodes).append("</").append(node.nome).append(">").append("\n");
            
        }
        return sb.toString();
    }
    
    private String commentsToString(String tabulacao, Node node) {
        StringBuilder sb = new StringBuilder();
        for (NodeContent n : node.comentarios)
            sb.append(tabulacao).append(n.getValue()).append("\n");
        
        return sb.toString();
    }
    
    private String propetiesToString(Node node) {
        StringBuilder sb = new StringBuilder();
        node.propriedades.keySet().forEach(key -> {
            sb.append(" ").append(key).append(" = '").append(node.propriedades.get(key)).append("'");
        });
        return sb.toString();
    }
    
    private String gerarTabulacao(int qtd) {
        String tabulacao = "";
        for (int i = 0; i < qtd; i++)
            tabulacao += "   ";
        return tabulacao;
    }
    
    public Node find(Find f) {
        return findImplementation(f, this);
    }
    
    private Node findImplementation(Find f, Node node) {
        
        if (f.find(node))
            return node;
        
        for (int i = 0; i < node.nodes.length; i++) {
            Node n = findImplementation(f, node.nodes[i]);
            if (n != null && f.find(n))
                return n;
        }
        return null;
    }
    
    public Node[] findAll(Find f) {
        
        return findNodesImplementation(f, this);
    }
    
    private Node[] findNodesImplementation(Find f, Node node) {
        Node[] nodes = new Node[0];
        
        if (f.find(node)) {
            nodes = new Node[1];
            nodes[0] = node;
        }
        
        for (int i = 0; i < node.nodes.length; i++)
            nodes = Utilitario.juntarNodes(nodes, findNodesImplementation(f, node.nodes[i]));
        return nodes;
    }
    
    public int propriedadesSize() {
        return tamanhoAtributo;
    }
    
    public boolean contains(Find f) {
        for (int i = 0; i < nodes.length; i++)
            if (f.find(nodes[i]))
                return true;
        return false;
        
    }
    
    public int size() {
        return tamanho;
    }
    
}

class Utilitario {
    
    public static boolean contemCaracterEspecial(String string) {
        for (char c : string.toCharArray())
            if (!Character.isLetter(c) && !Character.isDigit(c))
                return true;
        return false;
    }
    
    public static boolean contemCaracterEspecial(String string, boolean permirtir, char... caracter) {
        for (char c : string.toCharArray())
            if (!Character.isLetter(c) && !Character.isDigit(c))
                for (char i : caracter)
                    if (i == c && !permirtir)
                        return true;
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
