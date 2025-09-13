// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelthrowing_knife_Converted<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "throwing_knife_converted"), "main");
	private final ModelPart bone;

	public Modelthrowing_knife_Converted(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone",
				CubeListBuilder.create().texOffs(8, 4)
						.addBox(-9.0F, -2.0F, 7.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(10, 13)
						.addBox(-7.5F, -2.25F, 7.025F, 1.0F, 1.0F, 1.95F, new CubeDeformation(0.0F)).texOffs(10, 10)
						.addBox(-9.5F, -2.25F, 7.025F, 1.0F, 1.0F, 1.95F, new CubeDeformation(0.0F)).texOffs(8, 0)
						.addBox(-10.0F, -4.0F, 7.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 10)
						.addBox(-6.25F, -5.0F, 7.025F, 1.0F, 2.0F, 1.95F, new CubeDeformation(0.0F)).texOffs(6, 8)
						.addBox(-10.75F, -5.0F, 7.025F, 1.0F, 2.0F, 1.95F, new CubeDeformation(0.0F)).texOffs(20, 0)
						.addBox(-9.0F, -11.0F, 7.25F, 2.0F, 8.0F, 1.5F, new CubeDeformation(0.0F)).texOffs(8, 12)
						.addBox(-7.75F, -11.0F, 8.0F, 1.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 1)
						.addBox(-7.85F, -11.2F, 8.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(6, 0)
						.addBox(-9.0F, -11.4F, 8.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-9.15F, -11.2F, 8.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(6, 12)
						.addBox(-9.25F, -11.0F, 8.0F, 1.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(8.0F, 24.0F, -8.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}