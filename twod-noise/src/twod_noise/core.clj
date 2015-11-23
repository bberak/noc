(ns twod-noise.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; setup function returns initial state
  {:t 0})

(defn update-state [state]
  {:t (+ (:t state) 0.01)})

(defn get-pixel-brightness [x y t]
  (q/map-range (q/noise (* x 0.01) (* y 0.01) t) 0 1 0 255))

(defn set-pixel [pixels pixel-idx brightness]
  (aset-int pixels pixel-idx (q/color brightness)))

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 255)
  ; Update each pixel with a 2D noise value
  (let [max-rows (q/height)
        max-cols (q/width)
        pixels (q/pixels)
        t (:t state)]
    (loop [row 0]
      (loop [col 0]
        (let [pixel-idx (+ (* row max-cols) col)
              brightness (get-pixel-brightness col row t)]
          (set-pixel pixels pixel-idx brightness))
        (if (< col (dec max-cols))
          (recur (inc col))))
      (if (< row (dec max-rows))
        (recur (inc row)))))
  ; Update the pixels with changes
  (q/update-pixels))

(defn -main []
  (q/defsketch twod-noise
    :title "You spin my circle right round"
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
    :middleware [m/fun-mode]))
