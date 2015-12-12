(ns vector-line.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 60)
  {:mouse {:x 0
           :y 0}})

(defn multiply-vector [v n]
  {:x (* (:x v) n)
   :y (* (:y v) n)})

(defn subtract-vectors [v1 v2]
  {:x (- (:x v1) (:x v2))
   :y (- (:y v1) (:y v2))})

(defn update-state [state]
  {:mouse {:x (q/mouse-x)
           :y (q/mouse-y)}})

(defn draw-state [state]
  (q/background 240)
  (let [center {:x (/ (q/width) 2) :y (/ (q/height) 2)}
        scale 0.5
        mouse (multiply-vector (subtract-vectors (:mouse state) center) scale)]
    (q/translate (:x center) (:y center))
    (q/line 0
            0
            (:x mouse)
            (:y mouse))))

(defn -main []
  (q/defsketch vector-line
    :title "You spin my circle right round"
    :size [500 500]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
