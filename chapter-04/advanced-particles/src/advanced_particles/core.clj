(ns advanced-particles.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [advanced-particles.records.basic-particle-system :refer :all]
            [advanced-particles.records.vector2d :refer :all]
            [advanced-particles.records.square-particle :refer :all]
            [advanced-particles.records.triangular-particle :refer :all]
            [advanced-particles.protocols.particle-list :as pl]
            [advanced-particles.protocols.particle :as p]
            [advanced-particles.protocols.vector :as v]))

(defn setup []
  (q/frame-rate 60)
  {:particle-system (->BasicParticleSystem [])
   :gravity (->Vector2D 0 0.275)
   :wind (->Vector2D -0.08 0.015)})

(defn create-triangular-particle []
  (let [speed (if (q/mouse-pressed?) 0.25 1)]
    (->TriangularParticle (->Vector2D (q/mouse-x) (q/mouse-y)) (v/multiply (->Vector2D (q/random -1 1) (q/random -7 0)) speed) 255 0 speed 1)))

(defn update-state [{particle-system :particle-system gravity :gravity wind :wind}]
  (let [updated-system (-> particle-system 
                           (p/step [gravity wind])
                           (pl/append [(create-triangular-particle)]))]
    {:particle-system updated-system
     :gravity gravity
     :wind wind}))

(defn draw-state [{particle-system :particle-system}]
  (q/background 240)
  (p/render particle-system))

(defn -main []
  (q/defsketch basic-particles
    :title "Basic Particles"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
