(ns sliding-box.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [sliding-box.vectors :as v]
            [sliding-box.forces :as f]))

(defn create-plane [theta friction-coefficient gravity]
  (let [hyp (v/magnitude gravity)
        x (* hyp (q/sin theta))
        y (* hyp (q/cos theta))]
    {:theta theta
     :friction-coefficient friction-coefficient
     :normal-force (v/create (- x) y)}))

(defn create-box [plane mass speed]
  (let [theta (:theta plane)
        x (q/width)
        y (* x (q/tan theta))
        location (v/create x y)
        velocity (v/multiply (v/multiply (v/normalize location) speed) -1)]
    {:location location
     :velocity velocity
     :mass mass
     :drag-coefficient (* 0.001 mass)}))

(defn setup []
  (q/frame-rate 60)
  (let [gravity (v/create 0 -1)
        theta (q/radians 30)
        plane (create-plane theta 0.5 gravity)]
    {:plane plane
     :box (create-box plane 20 1.5)
     :gravity gravity}))

(defn keep-location-outside-plane [location plane]
  (let [plane-theta (:theta plane)
        x (:x location)
        y (:y location)
        location-theta (q/atan2 y x)]
    (if (< location-theta plane-theta)
      (v/create x (* x (q/tan plane-theta)))
      location)))

(defn update-state [{plane :plane box :box gravity :gravity}]
  (let [location (:location box)
        mass (:mass box)
        velocity (:velocity box)
        n-velocity (v/normalize velocity)
        normal-force (:normal-force plane)
        friction-coefficient (:friction-coefficient plane)
        friction (v/multiply (v/cross n-velocity normal-force) (* -1 friction-coefficient))
        drag-coefficient (:drag-coefficient box)
        speed (v/magnitude velocity)
        drag (v/multiply n-velocity (* -1 speed speed drag-coefficient))
        forces (v/add gravity friction drag)
        acceleration (v/divide forces mass)
        new-velocity (v/add velocity acceleration)
        new-location (v/add location new-velocity)]
    {:plane plane
     :box (assoc box :velocity new-velocity :location (keep-location-outside-plane  new-location plane))
     :gravity gravity}))

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
