(ns clouds.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.records.basic-particle-system :refer :all]
            [basic-particles.records.vector2d :refer :all]
            [clouds.puff :refer :all]
            [basic-particles.protocols.particle-list :as pl]
            [basic-particles.protocols.particle :as p]
            [basic-particles.protocols.vector :as v]))

(defn setup []
  (q/frame-rate 30)
  {:ps (->BasicParticleSystem [])})

(defn puff []
  (let [location (->Vector2D (/ (q/width) 2) (/ (q/height) 2))
        velocity (->Vector2D (q/random -3 3) (q/random -3 0))
        lifespan 255
        variance (q/random 0 25)]
    (->Puff location velocity lifespan variance)))

(defn update-state [{ps :ps}]
  (let [new-ps (-> ps
                   (p/step [(->Vector2D 0 0.05)])
                   (pl/append [(puff)]))]
    {:ps new-ps}))

(defn draw-state [{ps :ps}]
  (q/background 240)
  (p/render ps))

(defn -main []
  (q/defsketch clouds
    :title "Clouds"
    :size [800 600]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
