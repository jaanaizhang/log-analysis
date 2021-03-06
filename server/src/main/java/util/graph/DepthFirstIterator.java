
package util.graph;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;

/**
 * Iterates over the vertices in a directed graph in depth-first order.
 *
 * @param <V> Vertex type
 * @param <E> Edge type
 */
public class DepthFirstIterator<V, E extends DefaultEdge>
    implements Iterator<V> {
  private final Iterator<V> iterator;

  public DepthFirstIterator(DirectedGraph<V, E> graph, V start) {
    // Dumb implementation that builds the list first.
    iterator = buildList(graph, start).iterator();
  }

  private static <V, E extends DefaultEdge> List<V> buildList(
      DirectedGraph<V, E> graph, V start) {
    final List<V> list = Lists.newArrayList();
    buildListRecurse(list, Sets.<V>newHashSet(), graph, start);
    return list;
  }

  /** Creates an iterable over the vertices in the given graph in a depth-first
   * iteration order. */
  public static <V, E extends DefaultEdge> Iterable<V> of(
      DirectedGraph<V, E> graph, V start) {
    // Doesn't actually return a DepthFirstIterator, but a list with the same
    // contents, which is more efficient.
    return buildList(graph, start);
  }

  /** Populates a collection with the nodes reachable from a given node. */
  public static <V, E extends DefaultEdge> void reachable(Collection<V> list,
      final DirectedGraph<V, E> graph, final V start) {
    buildListRecurse(list, new HashSet<V>(), graph, start);
  }

  private static <V, E extends DefaultEdge> void buildListRecurse(
      Collection<V> list, Set<V> activeVertices, DirectedGraph<V, E> graph,
      V start) {
    if (!activeVertices.add(start)) {
      return;
    }
    list.add(start);
    List<E> edges = graph.getOutwardEdges(start);
    for (E edge : edges) {
      //noinspection unchecked
      buildListRecurse(list, activeVertices, graph, (V) edge.target);
    }
    activeVertices.remove(start);
  }

  public boolean hasNext() {
    return iterator.hasNext();
  }

  public V next() {
    return iterator.next();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}

// End DepthFirstIterator.java
