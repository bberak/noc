(ns dark-forces.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.protocols.particle :as p]
            [basic-particles.protocols.particle-list :as pl]
            [basic-particles.records.basic-particle-system :refer :all]
            [basic-particles.records.square-particle :refer :all]
            [basic-particles.records.vector2d :refer :all]))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  {:ps (->BasicParticleSystem [])})

(defn create-paricle []
  (let [location (->Vector2D (/ (q/width) 2) (/ (q/height) 2))
        lifespan 255
        velocity (->Vector2D (q/random -3 -1) (q/random -3 -1))
        angle (q/radians (q/random 0 360))]
    (->SquareParticle location velocity lifespan angle)))

(defn update-state [{ps :ps}]
  (let [gravity (->Vector2D 0 0.05)
        wind (->Vector2D -0.01 0)
        updated-ps (-> ps 
                     (p/step [gravity wind])
                     (pl/append [(create-paricle)]))]
    {:ps updated-ps}))

(defn draw-state [{ps :ps}]
  (q/background 240)
  (p/render ps))

(defn -main []
  (q/defsketch dark-forces
    :title "Dark Forces"
    :size [800 600]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
