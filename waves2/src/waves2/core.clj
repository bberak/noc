(ns waves2.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [waves2.vectors :as v]
            [waves2.forces :as f]
            [waves2.waves :as w]))

(defn setup []
  (q/frame-rate 60)
  {:waves [(assoc (w/sine-wave (v/create 50 100) 400 150 15 1.95 25 0) :render w/render)
           (assoc (w/cosine-wave (v/create 50 400) 400 250 30 0.95 20 -0.5) :render #(w/render-ellipses % 50 50 50 50 50 120))
           (assoc (w/cosine-wave (v/create 350 400) 400 250 30 0.95 5 -0.25) :render #(w/render-rects % 50 50 50 50 50 120))
           (assoc (w/cosine-wave (v/create 300 100) 400 250 45 0.95 20 0) :render #(w/render-rects-with-heading % 40 40 50 50 50 120))
           ]})

(defn update-state [{waves :waves}]
  {:waves (map (fn[w] (w/update-wave w)) waves)})

(defn draw-state [{waves :waves}]
  (q/background 255)
  (doseq [w waves]
    (let [render-func (:render w)]
      (render-func w))))

(defn -main []
  (q/defsketch waves2
    :title "Waves 2"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
