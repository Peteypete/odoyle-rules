(defmulti task first)

(defmethod task :default
  [[task-name]]
  (println "Unknown benchmark:" task-name)
  (System/exit 1))

(require '[odoyle.benchmark.simple])

(defmethod task "simple"
  [_]
  (odoyle.benchmark.simple/bench 10000))

(require '[odoyle.benchmark.dungeon-crawler.core])

(defmethod task "dungeon-crawler"
  [_]
  (odoyle.benchmark.dungeon-crawler.core/bench 100))

(require '[odoyle.benchmark.dungeon-crawler.clara])

(defmethod task "dungeon-crawler-clara"
  [_]
  (odoyle.benchmark.dungeon-crawler.clara/bench 100))

(task *command-line-args*)
