package nl.uva.alexandria.model.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.Metrics;

import java.util.*;

public class BenchmarkResult {
    private BenchmarkMetric benchmarkMic = new BenchmarkMetric();
    private BenchmarkMetric benchmarkAc = new BenchmarkMetric();

    public void addDependencyTreeResult(DependencyTreeResult rootNode) {
        this.benchmarkMic.addDependencyTreeResultMic(rootNode);
        this.benchmarkAc.addDependencyTreeResultAc(rootNode);
    }

    public BenchmarkMetric getBenchmarkMic() {
        return benchmarkMic;
    }

    public BenchmarkMetric getBenchmarkAc() {
        return benchmarkAc;
    }

    class BenchmarkMetric {
        private List<Integer> directDependencies = new ArrayList<>();
        private List<Map<Integer, Integer>> transitiveDependencies = new ArrayList<>();

        void addDependencyTreeResultMic(DependencyTreeResult rootNode) {
            this.addDependencyTreeResult(Metrics.MIC, rootNode);
        }

        void addDependencyTreeResultAc(DependencyTreeResult rootNode) {
            this.addDependencyTreeResult(Metrics.AC, rootNode);
        }

        public List<Integer> getDirectDependencies() {
            return directDependencies;
        }

        public List<Map<Integer, Integer>> getTransitiveDependencies() {
            return transitiveDependencies;
        }

        private void addDependencyTreeResult(Metrics metric, DependencyTreeResult rootNode) {
            List<DependencyTreeResult> directDependencyNodes = rootNode.getChildren();
            Queue<DependencyTreeResult> transitiveDependencyNodes = new ArrayDeque<>();

            directDependencyNodes.forEach(directDependencyNode -> {
                transitiveDependencyNodes.addAll(directDependencyNode.getChildren());
                if (metric == Metrics.MIC) {
                    directDependencies.add(directDependencyNode.getMicAtDistance().get(1));
                } else {
                    directDependencies.add(directDependencyNode.getAcAtDistance().get(1));
                }
            });

            while (!transitiveDependencyNodes.isEmpty()) {
                DependencyTreeResult transitiveDependencyNode = transitiveDependencyNodes.poll();
                transitiveDependencyNodes.addAll(transitiveDependencyNode.getChildren());
                if (metric == Metrics.MIC) {
                    transitiveDependencies.add(transitiveDependencyNode.getMicAtDistance());
                } else {
                    transitiveDependencies.add(transitiveDependencyNode.getAcAtDistance());
                }
            }
        }
    }
}
