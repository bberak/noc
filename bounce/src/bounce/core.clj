(ns bounce.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 60)
  (q/camera  1.5 -251.5 (/ (/ (q/height) 2.0) (Math/tan (/ (* Math/PI 60.0) 360.0))) ; eye
          0 0 0 ; centre
          0 1 0) ; up
  (q/sphere-detail 10)
  {:box 300
   :radius 20
   :loc {:x 0 :y 0 :z 0}
   :velocity {:x 3 :y 2 :z 1}
   :min-bounds {:x -150 :y -150 :z -150}
   :max-bounds {:x 150 :y 150 :z 150}})

(defn vector-ops [op v1 v2]
  {:x (op (:x v1) (:x v2))
   :y (op (:y v1) (:y v2))
   :z (op (:z v1) (:z v2))})

(defn limit-range [num min max]
  (cond 
    (< num min) min
    (> num max) max
    :else num))

(defn cap-vector [v v-min v-max]
  {:x (limit-range (:x v) (:x v-min) (:x v-max))
   :y (limit-range (:y v) (:y v-min) (:y v-max))
   :z (limit-range (:z v) (:z v-min) (:z v-max))})

(defn reflect-vector [v loc new-loc]
  {:x (if (= (:x loc) (:x new-loc)) (* -1 (:x v)) (:x v))
   :y (if (= (:y loc) (:y new-loc)) (* -1 (:y v)) (:y v))
   :z (if (= (:z loc) (:z new-loc)) (* -1 (:z v)) (:z v))})

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
  (q/translate (:x loc) (:y loc) (:x loc))
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
