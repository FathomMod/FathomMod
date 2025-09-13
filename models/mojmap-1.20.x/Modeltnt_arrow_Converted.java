// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modeltnt_arrow_Converted<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "tnt_arrow_converted"), "main");
	private final ModelPart Pain;

	public Modeltnt_arrow_Converted(ModelPart root) {
		this.Pain = root.getChild("Pain");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Pain = partdefinition.addOrReplaceChild("Pain",
				CubeListBuilder.create().texOffs(0, 12)
						.addBox(-8.1538F, 0.0F, -0.5F, 12.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 13)
						.addBox(-8.1538F, -0.5F, 0.0F, 12.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.6538F, -1.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.6538F, 0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(0.3462F, -2.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(0.3462F, 1.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(12, 15)
						.addBox(0.3462F, 0.0F, 1.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(9, 14)
						.addBox(0.3462F, 0.0F, -2.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(6, 15)
						.addBox(-0.6538F, 0.0F, 0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 15)
						.addBox(-0.6538F, 0.0F, -1.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(2.3462F, -2.5F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 9)
						.addBox(2.3461F, -0.5F, -2.5F, 0.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-14.1538F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.1538F, 8.0F, 0.5F, 0.0F, 0.0F, 1.5708F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Pain.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}