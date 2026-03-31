package com.example.aisearchlab.ui.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.aisearchlab.algorithms.SearchNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmGraphView extends View {

    private Paint nodePaint;
    private Paint edgePaint;
    private Paint textPaint;
    private Paint highlightPaint;

    private SearchNode rootNode;
    private List<SearchNode> openList = new ArrayList<>();
    private List<String> closedListIds = new ArrayList<>();
    
    // Map SearchNode to computed coordinates
    private Map<SearchNode, Point> nodePositions = new HashMap<>();
    
    // Configurable dimensions
    private final int NODE_RADIUS = 40;
    private final int VERTICAL_SPACING = 150;
    private final int HORIZONTAL_SPACING = 100;

    public AlgorithmGraphView(Context context) {
        super(context);
        init();
    }

    public AlgorithmGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nodePaint.setColor(Color.parseColor("#4CAF50")); // Green for explored
        nodePaint.setStyle(Paint.Style.FILL);

        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setColor(Color.parseColor("#FFC107")); // Amber for OPEN

        edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        edgePaint.setColor(Color.GRAY);
        edgePaint.setStrokeWidth(5f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    // Update the data to draw
    public void setGraphData(SearchNode root, List<SearchNode> open, List<String> closed) {
        this.rootNode = root;
        this.openList = open != null ? open : new ArrayList<>();
        this.closedListIds = closed != null ? closed : new ArrayList<>();
        
        // Recompute layout
        nodePositions.clear();
        if (rootNode != null) {
            computeLayout(rootNode, getWidth() / 2, 100, 0);
        }
        invalidate(); // Trigger redraw
    }

    // Recursive layout computation (Basic Tree Layout)
    private int computeLayout(SearchNode node, int x, int y, int depth) {
        if (node == null) return x;
        
        // If it's a leaf node we just place it
        // However, we don't naturally have a "children" list in SearchNode since it's built bottom-up.
        // For a true visualization, the viewmodel needs to capture the *tree* of explored nodes, 
        // not just a single goal path. 
        // For now, let's just lay out the nodes we *do* know about (the path from root).
        
        nodePositions.put(node, new Point(x, y));
        return x;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rootNode == null) return;

        // Draw edges first so they are under nodes
        for (Map.Entry<SearchNode, Point> entry : nodePositions.entrySet()) {
            SearchNode node = entry.getKey();
            Point pos = entry.getValue();
            
            if (node.parent != null && nodePositions.containsKey((SearchNode)node.parent)) {
                Point parentPos = nodePositions.get((SearchNode)node.parent);
                canvas.drawLine(parentPos.x, parentPos.y, pos.x, pos.y, edgePaint);
            }
        }

        // Draw nodes
        for (Map.Entry<SearchNode, Point> entry : nodePositions.entrySet()) {
            SearchNode node = entry.getKey();
            Point pos = entry.getValue();
            
            Paint currentPaint = nodePaint;
            
            // Highlight based on lists
            String id = node.state.getUniqueId();
            if (openList.contains(node)) {
                currentPaint = highlightPaint; // It's in OPEN list
            } else if (!closedListIds.contains(id)) {
                currentPaint.setColor(Color.LTGRAY); // Unexplored / generated but not visited
            } else {
                currentPaint.setColor(Color.parseColor("#4CAF50")); // Visited
            }

            canvas.drawCircle(pos.x, pos.y, NODE_RADIUS, currentPaint);
            
            // Draw a tiny label (e.g. cost or ID)
            String label = String.valueOf((int)node.pathCost);
            canvas.drawText(label, pos.x, pos.y + 10, textPaint);
        }
    }

    private static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }
}
