(ns random-splatter.app
  (:require [quil.core :as q]))

(defn setup []
  ; Clear the bg once
  (q/background 240)
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)

  {:x (/ (q/width) 2)
   :y (/ (q/height) 2)
   :color 127})

(defn update-state [state]
  (let [mean-width (/ (q/width) 2)
        mean-height (/ (q/height) 2)
        mean-color 127
        sd-width 120
        sd-height 80
        sd-color 45
        rand-x (q/random-gaussian)
        rand-y (q/random-gaussian)
        rand-color (q/random-gaussian)]
    {:x (+ (* sd-width rand-x) mean-width)
     :y (+ (* sd-height rand-y) mean-height)
     :color (+ (* sd-color rand-color) mean-color)}))

(defn draw-state [state]
  ; Set circle color.
  (q/fill (:color state) 255 255 30)
  ; No strokes on the circle
  (q/no-stroke)
  ; Draw the circle.
  (q/ellipse (:x state) (:y state) 16 16))
