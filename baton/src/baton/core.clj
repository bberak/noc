(ns baton.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  {:angle 0})

(defn update-state [state]
  {:angle (+ (:angle state) 0.1)})

(defn draw-state [state]
  (q/background 240)
  (q/fill 0 255 255)
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]       
    (q/rotate (:angle state))
    (q/line -100 0 100 0)
    (q/ellipse -100 0 20 20)
    (q/ellipse 100 0 20 20)))

(defn -main [] 
  (q/defsketch baton
  :title "Baton"
  :size [640 480]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode]))

