(ns basic-particles.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.particle-system]
            [basic-particles.vector]
            [basic-particles.particle]
            [basic-particles.protocols.particle-system-operations :refer :all])
  (:import [basic_particles.particle_system ParticleSystem]
           [basic_particles.vector Vector]
           [basic_particles.particle Particle]))

(defn setup []
  (q/frame-rate 60)
  {:particle-system (ParticleSystem. [])
   :gravity (Vector. 0 0.275)
   :wind (Vector. -0.08 0.015)})

(defn create-particle []
  (Particle. (Vector. (q/mouse-x) (q/mouse-y)) (Vector. (q/random -1 1) (q/random -7 0)) 255 0))

(defn update-state [{particle-system :particle-system gravity :gravity wind :wind}]
  (let [updated-system (-> particle-system 
                           (update-particles [gravity wind])
                           (add-particles [(create-particle)]))]
    {:particle-system updated-system
     :gravity gravity
     :wind wind}))

(defn draw-state [{particle-system :particle-system}]
  (q/background 240)
  (render-system particle-system))

(defn -main []
  (q/defsketch basic-particles
    :title "Basic Particles"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
