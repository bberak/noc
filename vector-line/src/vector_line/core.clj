(ns vector-line.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 60)
  {:origin {:x (/ (q/width) 2)
            :y (/ (q/height) 2)}
   :mouse {:x (/ (q/width) 2)
           :y (/ (q/height) 2)}})

(defn update-state [state]
  {:origin {:x (/ (q/width) 2)
            :y (/ (q/height) 2)}
   :mouse {:x (q/mouse-x)
           :y (q/mouse-y)}})

(defn draw-state [state]
  (q/background 240)
  (q/line (get-in state [:origin :x])
          (get-in state [:origin :y])
          (get-in state [:mouse :x])
          (get-in state [:mouse :y])))

(defn -main []
  (q/defsketch vector-line
    :title "You spin my circle right round"
    :size [500 500]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
