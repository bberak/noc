(ns spiral.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (q/no-stroke)
  (q/fill 20 255 255)
  {:r 0
   :theta 0})

(defn update-state [state]
  {:r (+ (:r state) 0.4)
   :theta (+ (:theta state) 0.05)})

(defn draw-state [state]
  (let [r (:r state)
        theta (:theta state)
        x (* r (q/cos theta))
        y (* r (q/sin theta))]
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      (q/ellipse x y 20 20))))

(defn -main []
  (q/defsketch spiral
    :title "You spin my circle right round"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
