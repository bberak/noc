(ns baton.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  {:angle 0
   :angular-velocity 0
   :angular-acceleration 0})

(defn limit [n min max]
  (cond (< n min) min (> n max) max :else n))

(defn update-state [{angle :angle velocity :angular-velocity acceleration :angular-acceleration}]
  (let [new-acceleration (+ acceleration 0.0001)
        new-velocity (limit (+ velocity new-acceleration) 0 0.75)
        new-angle (+ angle new-velocity)]
    {:angle new-angle
     :angular-velocity new-velocity
     :angular-acceleration new-acceleration}))

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

