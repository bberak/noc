(ns random-distribution.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 60)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; create seq of maps containing a num and count
  (vec (map (fn [n] {:count 0})
            (range 1 20))))

(defn update-state [state]
  ; Update the graph by selecting a new random number
  ; and updating the count on the corresponding key-value-pair
  (let [min-num 0
        max-num (count state)
        next-random-num (int (q/random min-num max-num))]
    (update-in state [next-random-num :count] inc)))

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; set a fill color for the bars
  (q/fill 175)
  ; Calculate the width of each bar
  (let [num-bars (count state)
        window-width (q/width)
        window-height (q/height)
        bar-width (/ window-width num-bars)]
    (doall (map-indexed (fn [idx num]
                   (let [x (* idx bar-width)
                         bar-height (:count num)
                         y (- window-height bar-height)]
                     (q/rect x y bar-width bar-height)))
                 state))))

(q/defsketch random-distribution
  :title "Random Distribution"
  :size [640 480]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
