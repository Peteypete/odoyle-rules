(ns odoyle.benchmark.dungeon-crawler.move
  (:require [odoyle.benchmark.dungeon-crawler.entities :as e]
            #?(:clj  [odoyle.benchmark.dungeon-crawler.macros-java :refer [math]]
               :cljs [odoyle.benchmark.dungeon-crawler.macros-js :refer-macros [math]])))

(def damping 0.1)
(def max-velocity 4)
(def max-enemy-velocity (/ max-velocity 2))
(def max-movement-per-frame 0.5)
(def min-movement-per-frame -0.5)
(def deceleration 0.8)
(def animation-secs 0.2)
(def max-attack-distance 0.5)
(def min-aggro-distance (- max-attack-distance 0.1))
(def max-aggro-distance 2)

(defn decelerate
  [velocity]
  (let [velocity (* velocity deceleration)]
    (if (< (math abs velocity) damping)
      0
      velocity)))

(defn calc-distance [x1 y1 x2 y2]
  (math abs (math sqrt (+ (math pow (- x1 x2) 2)
                          (math pow (- y1 y2) 2)))))

(defn get-enemy-velocity [{:keys [x-velocity y-velocity] :as enemy} player player-health distance-from-player]
  (or (when (> player-health 0)
        (cond
          (< min-aggro-distance distance-from-player max-aggro-distance)
          [(cond-> max-enemy-velocity
                   (< (:x player) (:x enemy))
                   (* -1))
           (cond-> max-enemy-velocity
                   (< (:y player) (:y enemy))
                   (* -1))]
          (<= distance-from-player min-aggro-distance)
          [0 0]))
      [(if (= 0 x-velocity)
         (-> (rand-int 3)
             (- 1)
             (* max-enemy-velocity))
         x-velocity)
       (if (= 0 y-velocity)
         (-> (rand-int 3)
             (- 1)
             (* max-enemy-velocity))
         y-velocity)]))

(defn get-direction
  [x-velocity y-velocity]
  (some->> e/velocities
           (filter (fn [[x y]]
                     (and (= x (int (math #?(:clj signum :cljs sign) (float x-velocity))))
                          (= y (int (math #?(:clj signum :cljs sign) (float y-velocity)))))))
           first
           (.indexOf e/velocities)
           (nth e/directions)))

(defn move [x y x-velocity y-velocity delta-time]
  (let [x-change (-> (* x-velocity delta-time)
                     (max min-movement-per-frame)
                     (min max-movement-per-frame))
        y-change (-> (* y-velocity delta-time)
                     (max min-movement-per-frame)
                     (min max-movement-per-frame))]
    {::e/x-velocity (decelerate x-velocity)
     ::e/y-velocity (decelerate y-velocity)
     ::e/x-change x-change
     ::e/y-change y-change
     ::e/x (+ x x-change)
     ::e/y (+ y y-change)}))

