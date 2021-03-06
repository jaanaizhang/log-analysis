
package util.graph;

import java.util.*;

/**
 * Iterates over the edges of a graph in topological order.
 *
 * @param <V> Vertex type
 * @param <E> Edge type
 */
public class TopologicalOrderIterator<V, E extends DefaultEdge>
    implements Iterator<V> {
  final Map<V, int[]> countMap = new HashMap<V, int[]>();
  final List<V> empties = new ArrayList<V>();
  private final DefaultDirectedGraph<V, E> graph;

  public TopologicalOrderIterator(DirectedGraph<V, E> graph) {
    this.graph = (DefaultDirectedGraph<V, E>) graph;
    populate(countMap, empties);
  }

  public static <V, E extends DefaultEdge> Iterable<V> of(
      final DirectedGraph<V, E> graph) {
    return new Iterable<V>() {
      public Iterator<V> iterator() {
        return new TopologicalOrderIterator<V, E>(graph);
      }
    };
  }

  private void populate(Map<V, int[]> countMap, List<V> empties) {
    for (V v : graph.vertexMap.keySet()) {
      countMap.put(v, new int[] {0});
    }
    for (DefaultDirectedGraph.VertexInfo<V, E> info
        : graph.vertexMap.values()) {
      for (E edge : info.outEdges) {
        //noinspection SuspiciousMethodCalls
        final int[] ints = countMap.get(edge.target);
        ++ints[0];
      }
    }
    for (Map.Entry<V, int[]> entry : countMap.entrySet()) {
      if (entry.getValue()[0] == 0) {
        empties.add(entry.getKey());
      }
    }
    countMap.keySet().removeAll(empties);
  }

  public boolean hasNext() {
    return !empties.isEmpty();
  }

  public V next() {
    V v = empties.remove(0);
    for (E o : graph.vertexMap.get(v).outEdges) {
      //noinspection unchecked
      final V target = (V) o.target;
      if (--countMap.get(target)[0] == 0) {
        countMap.remove(target);
        empties.add(target);
      }
    }
    return v;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  Set<V> findCycles() {
    while (hasNext()) {
      next();
    }
    return countMap.keySet();
  }
}

// End TopologicalOrderIterator.java
