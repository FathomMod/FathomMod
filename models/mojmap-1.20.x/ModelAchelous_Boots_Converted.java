// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelAchelous_Boots_Converted<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "achelous_boots_converted"), "main");
	private final ModelPart Boot;
	private final ModelPart Booot;

	public ModelAchelous_Boots_Converted(ModelPart root) {
		this.Boot = root.getChild("Boot");
		this.Booot = root.getChild("Booot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Boot = partdefinition.addOrReplaceChild("Boot",
				CubeListBuilder.create().texOffs(5, 3)
						.addBox(-1.0F, 1.0F, -1.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 0)
						.addBox(-2.0F, -1.0F, -1.75F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 12)
						.addBox(-2.0F, -3.0F, -1.25F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(6, 4)
						.addBox(2.0001F, -3.0F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 15)
						.addBox(2.0F, -3.0F, -0.75F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 12)
						.addBox(-2.0F, -3.0F, -0.75F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-2.0001F, -3.0F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(12, 3)
						.addBox(-1.0F, -3.0F, 3.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(3, 0)
						.addBox(-1.0F, 1.0F, 0.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offset(1.5F, 23.0F, -1.5F));

		PartDefinition Booot = partdefinition.addOrReplaceChild("Booot",
				CubeListBuilder.create().texOffs(12, 0)
						.addBox(-1.0F, -1.0F, -0.75F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 12)
						.addBox(-1.0F, -3.0F, -0.25F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(6, 4)
						.addBox(3.0001F, -3.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 15)
						.addBox(3.0F, -3.0F, 0.25F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 12)
						.addBox(-1.0F, -3.0F, 0.25F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-1.0001F, -3.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(12, 3)
						.addBox(0.0F, -3.0F, 4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(3, 0)
						.addBox(0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(5, 3)
						.addBox(0.0F, 1.0F, 0.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-4.35F, 23.0F, -2.5F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Boot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Booot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.Booot.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
		this.Boot.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
	}
}