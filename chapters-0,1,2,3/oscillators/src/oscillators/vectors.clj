(ns oscillators.vectors
  (:require [quil.core :as q]))

(defn -vector-ops [op & args]
  (reduce (fn [{x1 :x y1 :y} {x2 :x y2 :y}]
              {:x (op x1 x2)
               :y (op y1 y2)})
          args))

(defn add [& args]
  (apply -vector-ops + args))

(defn subtract [& args]
  (apply -vector-ops - args))

(defn cross [& args]
  (apply -vector-ops * args))

(defn multiply [{x :x y :y} n]
  {:x (* x n)
   :y (* y n)})

(defn divide [{x :x y :y} n]
  (if (zero? n)
    {:x x
     :y y}
	{:x (/ x n)
	 :y (/ y n)}))

(defn magnitude [{x :x y :y}]
  (q/sqrt (+ (q/sq x) (q/sq y))))

(defn distance [{x1 :x y1 :y} {x2 :x y2 :y}]
  (q/dist x1 y1 x2 y2))

(defn normalize [v]
  (let [mag (magnitude v)]
    (divide v mag)))

(defn constrain-magnitude [v n]
  (let [mag (magnitude v)]
    (if (> mag n)
      (multiply (normalize v) n)
      v)))

(defn random []
  (let [v {:x (q/random-gaussian) :y (q/random-gaussian)}]
    (normalize v)))

(defn create [x y]
  {:x x :y y})

(defn in-range? [n min max]
  (and (>= n min) (<= n max)))

(defn outside-range? [n min max]
  (not (in-range? n min max)))

(defn within-bounds?
  ([v] (within-bounds? v {:min 0 :max (q/width)} {:min 0 :max (q/height)}))
  ([{x :x y :y} {min-x :min max-x :max} {min-y :min max-y :max}]
   (every? true? [(in-range? x min-x max-x) (in-range? y min-y max-y)])))

(defn breached-bounds? 
  ([v] (not (within-bounds? v)))
  ([v x-range y-range] (not (within-bounds? v x-range y-range))))

(defn constrain-bounds 
  ([v] (constrain-bounds v {:min 0 :max (q/width)} {:min 0 :max (q/height)}))
  ([{x :x y :y} {min-x :min max-x :max} {min-y :min max-y :max}]
    (let [new-x (cond (< x min-x) min-x (> x max-x) max-x :else x)
          new-y (cond (< y min-y) min-y (> y max-y) max-y :else y)]
      {:x new-x
       :y new-y})))

(defn bounce
  ([location velocity] (bounce location velocity {:min 0 :max (q/width)} {:min 0 :max (q/height)}))
  ([location velocity x-range y-range]
   (if (breached-bounds? location x-range y-range)
     (let [new-location (constrain-bounds location x-range y-range)
           new-velocity {:x (if (in-range? (:x location) (:min x-range) (:max x-range)) (:x velocity) (* -1 (:x velocity)))
                         :y (if (in-range? (:y location) (:min y-range) (:max y-range)) (:y velocity) (* -1 (:y velocity)))}]
       {:location new-location
        :velocity new-velocity})
     {:location location 
      :velocity velocity})))














