(ns clouds.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.records.vector2d :refer :all]
            [clouds.cloud-system :refer :all]
            [clouds.puff :refer :all]
            [basic-particles.protocols.particle-list :as pl]
            [basic-particles.protocols.particle :as p]
            [basic-particles.protocols.vector :as v]))

(defn setup []
  (q/frame-rate 30)
  (q/blend-mode :add)
  {:ps (->CloudSystem (->Vector2D (/ (q/width) 2) (/ (q/height) 2)) 
                      [(->CloudSystem (->Vector2D -40 -30) [])
                       (->CloudSystem (->Vector2D 40 30) [])
                       (->CloudSystem (->Vector2D -80 30) [])
                       (->CloudSystem (->Vector2D 80 -30) [])])})

(defn update-state [{ps :ps}]
  (let [new-ps (p/step ps  [])] ;; Comet effect [(->Vector2D 0.1 0.1)]
    {:ps new-ps}))

(defn draw-state [{ps :ps}]
  (q/background 74 144 226)
  (p/render ps))

(defn -main []
  (q/defsketch clouds
    :title "Clouds"
    :size [800 600]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]
    :renderer :p2d))
