(ns bounce.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 60)
  (q/camera 1.5 -251.5 (/ (/ (q/height) 2.0) (Math/tan (/ (* Math/PI 60.0) 360.0))) ; eye
          0 0 0 ; centre
          0 1 0) ; up
  (q/sphere-detail 10)
  {:box 300
   :radius 20
   :loc {:x 0 :y 0 :z 0}
   :velocity {:x 12 :y 8 :z -6}
   :min-bounds {:x -130 :y -130 :z -130}
   :max-bounds {:x 130 :y 130 :z 130}})

(defn vector-ops [op v1 v2]
  (into {} (map (fn [[key val]]
       [key (op val (key v2))])
       v1)))

(defn limit-range [num min max]
  (cond 
    (< num min) min
    (> num max) max
    :else num))

(defn cap-vector [v v-min v-max]
  (into {} (map (fn [[key val]]
         [key (limit-range val (key v-min) (key v-max))])
       v)))

(defn reflect-vector [v loc new-loc]
  (into {} (map (fn [[key val]]
         [key (if (= (key loc) (key new-loc)) (* -1 val) val)])
       v)))

(defn update-state [{box :box radius :radius loc :loc velocity :velocity min-bounds :min-bounds max-bounds :max-bounds}]
  (let [new-loc (cap-vector (vector-ops + loc velocity) min-bounds max-bounds)
        new-velocity (reflect-vector velocity loc new-loc)]
    {:box box
     :radius radius
     :loc new-loc
     :velocity new-velocity
     :min-bounds min-bounds
     :max-bounds max-bounds}))

(defn draw-state [{box :box radius :radius loc :loc}]
  (q/background 240)
  (q/no-fill)
  (q/box box)
  (q/push-matrix)
  (q/translate (:x loc) (:y loc) (:z loc))
  (q/sphere radius)
  (q/pop-matrix))

(defn -main []
  (q/defsketch bounce
    :title "Bouncey bouncey bouncey"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :renderer :opengl
    :middleware [m/fun-mode]))
