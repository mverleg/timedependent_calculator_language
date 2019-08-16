package nl.markv.tdcl;

import java.util.Collections;
import java.util.List;

import nl.markv.tdcl.data.Node;

import static nl.markv.tdcl.data.Dependency.Direction.Current;
import static nl.markv.tdcl.data.Dependency.cur;
import static nl.markv.tdcl.data.Dependency.prev;

public class Main {

    public static void main(String[] args) {

        var n1 = new Node("Input1");
        var n2 = new Node("Input2");
        var n3 = new Node("Comp3", cur(n1), prev(n2));
        var n4 = new Node("Comp4", cur(n3)).selfRef(Current);
        var n5 = new Node("Final5", cur(n4));
        List<Node> finals = Collections.singletonList(n5);
    }
}
