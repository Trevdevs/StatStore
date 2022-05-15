package dev.trevdevs.statstore;

import org.joml.Vector3f;

public class GameObject
{
    private final Mesh mesh;

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public GameObject(Mesh mesh)
    {
        this.mesh = mesh;
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1,1,1);
    }

    public Mesh getMesh()
    {
        return mesh;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public Vector3f getRotation()
    {
        return rotation;
    }

    public Vector3f getScale()
    {
        return scale;
    }

    public void translate(float x, float y, float z)
    {
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public void rotate(float x, float y, float z)
    {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }

    public void setScale(float x, float y, float z)
    {
        scale.x = x;
        scale.y = y;
        scale.z = z;
    }
}
