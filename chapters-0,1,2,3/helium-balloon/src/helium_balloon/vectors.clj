(ns helium-balloon.vectors
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

(defn multiply [{x :x y :y} n]
  {:x (* x n)
   :y (* y n)})

(defn divide [{x :x y :y} n]
  (if (= n 0.0)
    {:x x
     :y y}
	{:x (/ x n)
	 :y (/ y n)}))

(defn magnitude [{x :x y :y}]
  (q/sqrt (+ (q/pow x 2) (q/pow y 2))))

(defn normalize [v]
  (let [mag (magnitude v)]
    (divide v mag)))

(defn limit [v n]
  (let [mag (magnitude v)]
    (if (> mag n)
      (multiply (normalize v) n)
      v)))

(defn random []
  (let [v {:x (q/random-gaussian) :y (q/random-gaussian)}]
    (normalize v)))

(defn create [x y]
  {:x x :y y})

(defn within-bounds 
  ([v] (within-bounds v {:min-x 0 :max-x (q/width)} {:min-y 0 :max-y (q/height)}))
  ([{x :x y :y} {min-x :min-x max-x :max-x} {min-y :min-y max-y :max-y}]
    (let [new-x (cond (< x min-x) min-x (> x max-x) max-x :else x)
          new-y (cond (< y min-y) min-y (> y max-y) max-y :else y)]
      {:x new-x
       :y new-y})))