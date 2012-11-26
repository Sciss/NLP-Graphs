package no.roek.nlpgraphs.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Edge {

	private String id;
	private Node from;
	private Node to;
//	private String to, from;
	private List<String> attributes;
	
	public Edge(String id, Node from, Node to, List<String> attributes) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.attributes = attributes;
	}
	
	public Edge(String id, Node from, Node to, String[] attributes) {
		this(id, from, to, Arrays.asList(attributes));
	}
	
//	public Edge(String id, String from, String to, List<String> attributes) {
//		this.id = id;
//		this.from = from;
//		this.to = to;
//		this.attributes = attributes;
//	}
	
//	public Edge(String id, String from, String to) {
//		this(id, from, to, new ArrayList<String>());
//	}
	public Edge(String id, Node from, Node to) {
		this(id, from, to, new ArrayList<String>());
	}
	
	public String getId() {
		return id;
	}
	
	

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}
	

//	public String getTo() {
//		return to;
//	}
//
//	public String getFrom() {
//		return from;
//	}

	public List<String> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(String attr) {
		attributes.add(attr);
	}
	
//	@Override
//	public String toString() {
//		return from.getId()+"-"+to.getId();
//	}
	@Override
	public String toString() {
		return from + "-" +to;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(getClass() == obj.getClass()) {
			Edge other = (Edge) obj;
//			return (from.equals(other.getFrom())) && (to.equals(other.getTo()) && (attributes.equals(other.attributes)));
//			return (from.equals(other.getFrom())) && (to.equals(other.getTo()));
			return attributesEqual(other) && from.equals(other.from);
		}
		return false;
	}
	
	public boolean attributesEqual(Edge other) {
		for (int i = 0; i < attributes.size(); i++) {
			if(!attributes.get(i).equals(other.getAttributes().get(i))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
//		return from.hashCode() * to.hashCode() * attributes.hashCode();
//		return from.hashCode() * to.hashCode();
		return attributes.hashCode();
	}
}
