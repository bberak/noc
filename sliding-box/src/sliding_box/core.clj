(ns sliding-box.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [sliding-box.vectors :as v]
            [sliding-box.forces :as f]))

(defn create-box [plane mass speed]
  (let [theta (:theta plane)
        x (- (q/width) mass)
        y (* x (q/tan theta))
        location (v/create x y)
        velocity (v/multiply (v/multiply (v/normalize location) speed) -1)]
    {:location location
     :velocity velocity
     :mass mass}))

(defn setup []
  (q/frame-rate 30)
  (let [plane  {:theta (q/radians 30)
                :friction-coefficient 0.5}]
    {:plane plane
     :box (create-box plane 20 3)
     :gravity (v/create 0 -1)}))

(defn update-state [state]
  state)

(defn render-plane [plane]
  (let [x1 0
        y1 0
        x2 (q/width)
        y2 0
        x3 (q/width)
        theta (:theta plane)
        y3 (* x3 (q/tan theta))]
    (q/triangle x1 y1 x2 y2 x3 y3)))

(defn render-box-along-plane [box plane]
  (let [location (:location box)
        side (* (:mass box) 5)]
    (q/with-translation [(:x location) (:y location)]
      (q/with-rotation [(:theta plane)]
        (q/rect 0 0 side side)))))

(defn draw-state [{plane :plane box :box}]
  ;; Flip the y-axis and reset origin back to bottom-left
  (q/scale 1 -1)
  (q/translate 0 (* -1 (q/height)))
  (q/background 240)
  (render-plane plane)
  (render-box-along-plane box plane))

(defn -main []
  (q/defsketch sliding-box
    :title "Slding Box"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
